package com.nttdata.bc19.mswallet.service.impl;

import com.nttdata.bc19.mswallet.exception.ModelNotFoundException;
import com.nttdata.bc19.mswallet.model.TransactionWallet;
import com.nttdata.bc19.mswallet.model.TransactionWalletCoin;
import com.nttdata.bc19.mswallet.model.Wallet;
import com.nttdata.bc19.mswallet.repository.ITransactionWalletCoinRepository;
import com.nttdata.bc19.mswallet.repository.ITransactionWalletRepository;
import com.nttdata.bc19.mswallet.repository.IWalletRepository;
import com.nttdata.bc19.mswallet.request.TransactionWalletCoinRequest;
import com.nttdata.bc19.mswallet.request.TransactionWalletRequest;
import com.nttdata.bc19.mswallet.request.WalletRequest;
import com.nttdata.bc19.mswallet.service.IWalletService;
import com.nttdata.bc19.mswallet.util.LogMessage;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@Service
public class WalletServiceImpl implements IWalletService{

    private final Logger LOGGER = LoggerFactory.getLogger("WalletLog");

    private final String TRANSACTIONSAVESUCCESS = "SAVESUCCESS";

    @Autowired
    IWalletRepository iWalletRepository;

    @Autowired
    ITransactionWalletRepository iTransactionWalletRepository;

    @Autowired
    ITransactionWalletCoinRepository iTransactionWalletCoinRepository;

    @Override
    public Mono<Wallet> create(WalletRequest walletRequest) {
        Wallet wallet = new Wallet();
        wallet.setId(new ObjectId().toString());
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setDocumentType(walletRequest.getDocumentType());
        wallet.setDocumentNumber(walletRequest.getDocumentNumber());
        wallet.setEmail(walletRequest.getEmail());
        wallet.setPhone(walletRequest.getPhone());
        wallet.setImei(walletRequest.getImei());
        wallet.setAmount(0);
        return iWalletRepository.save(wallet);
    }

    @Override
    public Mono<Wallet> update(Wallet wallet) {
        wallet.setUpdatedAt(LocalDateTime.now());
        return iWalletRepository.save(wallet);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return iWalletRepository.deleteById(id);
    }

    @Cacheable(value = "walletCache")
    @Override
    public Mono<Wallet> findById(String id) {
        return iWalletRepository.findById(id);
    }

    @Override
    public Flux<Wallet> findAll() {
        return iWalletRepository.findAll();
    }

    @Cacheable(value = "transactionWalletCache")
    @Override
    public Mono<TransactionWallet> transactionWallet(TransactionWalletRequest transactionWalletRequest) {
        return iWalletRepository.findByPhone(transactionWalletRequest.getPhoneSource())
                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Source Not Found.")))
                .flatMap(walletSourceResponse ->{
                        if(walletSourceResponse.getAmount() < transactionWalletRequest.getAmount())
                            return Mono.error(new ModelNotFoundException("Insufficient balance."));
                        else
                            return iWalletRepository.findByPhone(transactionWalletRequest.getPhoneDestiny())
                                    .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Destiny Not Found.")))
                                    .flatMap(walletDestinyResponse -> {
                                        walletSourceResponse.setAmount(walletSourceResponse.getAmount() - transactionWalletRequest.getAmount());
                                        walletSourceResponse.setUpdatedAt(LocalDateTime.now());
                                        return this.update(walletSourceResponse)
                                                .switchIfEmpty(Mono.error(new ModelNotFoundException("Id Wallet Not Found.")))
                                                .flatMap(walletSourceUpdateResponse -> {
                                                    walletDestinyResponse.setAmount(walletDestinyResponse.getAmount() + transactionWalletRequest.getAmount());
                                                    walletDestinyResponse.setUpdatedAt(LocalDateTime.now());
                                                    return this.update(walletDestinyResponse)
                                                            .switchIfEmpty(Mono.error(new ModelNotFoundException("Id Wallet Not Found.")))
                                                            .flatMap(walletDestinyUpdateResponse -> {
                                                                TransactionWallet transactionWallet = new TransactionWallet();
                                                                transactionWallet.setId(new ObjectId().toString());
                                                                transactionWallet.setCreatedAt(LocalDateTime.now());
                                                                transactionWallet.setPhoneSource(transactionWalletRequest.getPhoneSource());
                                                                transactionWallet.setPhoneDestiny(transactionWalletRequest.getPhoneDestiny());
                                                                transactionWallet.setAmount(transactionWalletRequest.getAmount());
                                                                return iTransactionWalletRepository.save(transactionWallet).doOnSuccess(this.doOnSucess(TRANSACTIONSAVESUCCESS));
                                                            });
                                                });
                                    });
                        });
    }

    @Cacheable(value = "transactionWalletCoinCache")
    @Override
    public Mono<TransactionWalletCoin> transactionWalletCoin(TransactionWalletCoinRequest transactionWalletRequest) {
        return iWalletRepository.findByPhone(transactionWalletRequest.getPhoneSource())
                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Source Not Found.")))
                .flatMap(walletSourceResponse ->{
                    if(walletSourceResponse.getAmount() < transactionWalletRequest.getAmountSource())
                        return Mono.error(new ModelNotFoundException("Insufficient balance."));
                    else
                        return iWalletRepository.findByPhone(transactionWalletRequest.getPhoneDestiny())
                                .switchIfEmpty(Mono.error(new ModelNotFoundException("Phone Destiny Not Found.")))
                                .flatMap(walletDestinyResponse -> {
                                    walletSourceResponse.setAmount(walletSourceResponse.getAmount() - transactionWalletRequest.getAmountSource());
                                    walletSourceResponse.setUpdatedAt(LocalDateTime.now());
                                    return this.update(walletSourceResponse)
                                            .switchIfEmpty(Mono.error(new ModelNotFoundException("Id Wallet Not Found.")))
                                            .flatMap(walletSourceUpdateResponse -> {
                                                walletDestinyResponse.setAmount(walletDestinyResponse.getAmount() + transactionWalletRequest.getAmountDestiny());
                                                walletDestinyResponse.setUpdatedAt(LocalDateTime.now());
                                                return this.update(walletDestinyResponse)
                                                        .switchIfEmpty(Mono.error(new ModelNotFoundException("Id Wallet Not Found.")))
                                                        .flatMap(walletDestinyUpdateResponse -> {
                                                            TransactionWalletCoin transactionWalletCoin = new TransactionWalletCoin();
                                                            transactionWalletCoin.setId(new ObjectId().toString());
                                                            transactionWalletCoin.setCreatedAt(LocalDateTime.now());
                                                            transactionWalletCoin.setPhoneSource(transactionWalletRequest.getPhoneSource());
                                                            transactionWalletCoin.setPhoneDestiny(transactionWalletRequest.getPhoneDestiny());
                                                            transactionWalletCoin.setAmountSource(transactionWalletRequest.getAmountSource());
                                                            transactionWalletCoin.setAmountDestiny(transactionWalletRequest.getAmountDestiny());
                                                            return iTransactionWalletCoinRepository.save(transactionWalletCoin).doOnSuccess(this.doOnSucessCoin(TRANSACTIONSAVESUCCESS));
                                                        });
                                            });
                                });
                });
    }

    private Consumer<TransactionWallet> doOnSucess(String idLogMessage){
        return new Consumer<TransactionWallet>() {
            @Override
            public void accept(TransactionWallet transactionWallet) {
                LOGGER.info(LogMessage.logMessage.get(idLogMessage));
            }
        };
    }

    private Consumer<TransactionWalletCoin> doOnSucessCoin(String idLogMessage){
        return new Consumer<TransactionWalletCoin>() {
            @Override
            public void accept(TransactionWalletCoin transactionWalletCoin) {
                LOGGER.info(LogMessage.logMessage.get(idLogMessage));
            }
        };
    }
}
