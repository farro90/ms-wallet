package com.nttdata.bc19.mswallet.service.impl;

import com.nttdata.bc19.mswallet.exception.ModelNotFoundException;
import com.nttdata.bc19.mswallet.model.*;
import com.nttdata.bc19.mswallet.producer.KafkaStringProducer;
import com.nttdata.bc19.mswallet.repository.*;
import com.nttdata.bc19.mswallet.request.*;
import com.nttdata.bc19.mswallet.service.ICoinService;
import com.nttdata.bc19.mswallet.service.IWalletService;
import com.nttdata.bc19.mswallet.util.LogMessage;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
public class CoinServiceImpl implements ICoinService {

    private final Logger LOGGER = LoggerFactory.getLogger("CoinLog");

    private final String TRANSACTIONSAVESUCCESS = "SAVESUCCESS";

    @Autowired
    ICoinRepository iCoinRepository;

    @Autowired
    IWalletRepository iWalletRepository;

    @Autowired
    IRateCoinRepository iRateCoinRepository;

    @Autowired
    IRequestBuyCoinRepository iRequestBuyCoinRepository;

    @Autowired
    ITransactionCoinRepository iTransactionCoinRepository;

    @Autowired
    IWalletService walletService;

    private final KafkaStringProducer kafkaStringProducer;

    public CoinServiceImpl(KafkaStringProducer kafkaStringProducer) {
        this.kafkaStringProducer = kafkaStringProducer;
    }


    @Override
    public Mono<ClientCoin> create(CoinRequest coinRequest) {
        return iWalletRepository.findByPhone(coinRequest.getPhone())
                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Not Found.")))
                .flatMap(walletResponse -> {
                    ClientCoin clientCoin = new ClientCoin();
                    clientCoin.setId(new ObjectId().toString());
                    clientCoin.setCreatedAt(LocalDateTime.now());
                    clientCoin.setDocumentType(coinRequest.getDocumentType());
                    clientCoin.setDocumentNumber(coinRequest.getDocumentNumber());
                    clientCoin.setPhone(coinRequest.getPhone());
                    clientCoin.setEmail(coinRequest.getEmail());
                    clientCoin.setCoins(0);
                    this.kafkaStringProducer.sendMessage("Client count created");
                    return iCoinRepository.save(clientCoin);
                });

    }

    @Override
    public Mono<ClientCoin> update(ClientCoin clientCoin) {
        clientCoin.setUpdatedAt(LocalDateTime.now());
        return iCoinRepository.save(clientCoin);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return iCoinRepository.deleteById(id);
    }

    @Override
    public Mono<ClientCoin> findById(String id) {
        return iCoinRepository.findById(id);
    }

    @Override
    public Flux<ClientCoin> findAll() {
        return iCoinRepository.findAll();
    }

    @Override
    public Flux<RateCoin> getRate() {
        return iRateCoinRepository.findAll();
    }

    @Override
    public Mono<RateCoin> setRateCoin(RateCoin rateCoin) {
        return iRateCoinRepository.findAll().filter(f -> f.getRateType().getName().equals(rateCoin.getRateType().getName())).singleOrEmpty()
                .switchIfEmpty(iRateCoinRepository.save(rateCoin))
                .flatMap(rateCoinResponse -> {
                    rateCoinResponse.setRate(rateCoin.getRate());
                    return iRateCoinRepository.save(rateCoinResponse);
                });
    }

    @Override
    public Mono<RequestBuyCoin> requestBuyCoin(RequestBuyCoinRequest requestBuyCoinRequest) {
        return iCoinRepository.findByPhone(requestBuyCoinRequest.getPhoneClientCoin())
                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Not Found.")))
                .flatMap(clientCoinResponse -> {
                    RequestBuyCoin requestBuyCoin = new RequestBuyCoin();
                    requestBuyCoin.setCoinsToBuy(requestBuyCoinRequest.getCoinsToBuy());
                    requestBuyCoin.setClientCoin(clientCoinResponse);
                    requestBuyCoin.setAccepted(false);
                    return iRequestBuyCoinRepository.save(requestBuyCoin);
                });
    }

    @Override
    public Flux<RequestBuyCoin> findAllRequestBuyCoin() {
        return iRequestBuyCoinRepository.findAll().filter(f -> !f.isAccepted());
    }

    @Override
    public Mono<TransactionCoin> acceptRequestBuyCoin(AcceptRequestBuyCoinRequest acceptRequestBuyCoinRequest) {
        return iRequestBuyCoinRepository.findById(acceptRequestBuyCoinRequest.getIdRequestBuyCoin())
                .switchIfEmpty(Mono.error(new ModelNotFoundException("Request Buy Not Found.")))
                .flatMap(requestBuyCoinResponse -> {
                            return iRateCoinRepository.findAll().filter(f -> f.getRateType().getName().equals("COMPRA")).singleOrEmpty()
                                    .switchIfEmpty(Mono.error(new ModelNotFoundException("Rate buy Noy Found.")))
                                    .flatMap(rateBuyCoin -> {
                                        return iRateCoinRepository.findAll().filter(f -> f.getRateType().getName().equals("VENTA")).singleOrEmpty()
                                                .switchIfEmpty(Mono.error(new ModelNotFoundException("Rate sale Noy Found.")))
                                                .flatMap(rateSaleCoin -> {
                                                    TransactionWalletCoinRequest transactionWalletCoinRequest = new TransactionWalletCoinRequest();
                                                    transactionWalletCoinRequest.setAmountSource(requestBuyCoinResponse.getCoinsToBuy() * rateBuyCoin.getRate());
                                                    transactionWalletCoinRequest.setAmountDestiny(requestBuyCoinResponse.getCoinsToBuy() * rateSaleCoin.getRate());
                                                    transactionWalletCoinRequest.setPhoneDestiny(acceptRequestBuyCoinRequest.getPhone());
                                                    transactionWalletCoinRequest.setPhoneSource(requestBuyCoinResponse.getClientCoin().getPhone());

                                                    return walletService.transactionWalletCoin(transactionWalletCoinRequest)
                                                            .switchIfEmpty(Mono.error(new ModelNotFoundException("Error in transaction wallet.")))
                                                            .flatMap(transactionWalletCoin -> {
                                                                return this.transferCoin(acceptRequestBuyCoinRequest, requestBuyCoinResponse);
                                                            });
                                                });
                                    });
                        });
    }

    private Mono<TransactionCoin> transferCoin(AcceptRequestBuyCoinRequest acceptRequestBuyCoinRequest, RequestBuyCoin requestBuyCoin){
        return iCoinRepository.findByPhone(acceptRequestBuyCoinRequest.getPhone())
                    .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Sale Coin Account Not Found.")))
                    .flatMap(clientSaleCoinResponse -> {
                        if (clientSaleCoinResponse.getCoins() < requestBuyCoin.getCoinsToBuy())
                            return Mono.error(new ModelNotFoundException("Insufficient coins."));
                        return iCoinRepository.findByPhone(requestBuyCoin.getClientCoin().getPhone())
                                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Buy Coin Account Not Found.")))
                                .flatMap(clientBuyCoinResponse -> {
                                    clientSaleCoinResponse.setCoins(clientSaleCoinResponse.getCoins() - requestBuyCoin.getCoinsToBuy());
                                    clientSaleCoinResponse.setUpdatedAt(LocalDateTime.now());
                                    return iCoinRepository.save(clientSaleCoinResponse)
                                            .switchIfEmpty(Mono.error(new ModelNotFoundException("Error in save transaction coin.")))
                                            .flatMap(clientSaleCoinUpdatedResponse -> {
                                                clientBuyCoinResponse.setCoins(clientBuyCoinResponse.getCoins() + requestBuyCoin.getCoinsToBuy());
                                                clientBuyCoinResponse.setUpdatedAt(LocalDateTime.now());
                                                return iCoinRepository.save(clientBuyCoinResponse)
                                                        .switchIfEmpty(Mono.error(new ModelNotFoundException("Error in save transaction coin.")))
                                                        .flatMap(clientBuyCoinUpdatedResponse -> {
                                                            requestBuyCoin.setAccepted(true);
                                                            return iRequestBuyCoinRepository.save(requestBuyCoin)
                                                                    .switchIfEmpty(Mono.error(new ModelNotFoundException("Error in update request coin.")))
                                                                    .flatMap(requestBuyCoinUpdate -> {
                                                                        TransactionCoin transactionCoin = new TransactionCoin();
                                                                        transactionCoin.setIdRequestBuyCoin(acceptRequestBuyCoinRequest.getIdRequestBuyCoin());
                                                                        transactionCoin.setRequestBuyCoin(requestBuyCoinUpdate);
                                                                        transactionCoin.setIdClientCoinSale(clientSaleCoinResponse.getId());
                                                                        transactionCoin.setClientCoinSale(clientSaleCoinResponse);
                                                                        transactionCoin.setCreatedAt(LocalDateTime.now());
                                                                        transactionCoin.setId(new ObjectId().toString());

                                                                        return iTransactionCoinRepository.save(transactionCoin);
                                                                    });
                                                        });
                                            });
                                });
                    });
    }
}
