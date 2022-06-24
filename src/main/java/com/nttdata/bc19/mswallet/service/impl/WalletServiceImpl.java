package com.nttdata.bc19.mswallet.service.impl;

import com.nttdata.bc19.mswallet.exception.ModelNotFoundException;
import com.nttdata.bc19.mswallet.model.TransactionWallet;
import com.nttdata.bc19.mswallet.model.Wallet;
import com.nttdata.bc19.mswallet.repository.ITransactionWalletRepository;
import com.nttdata.bc19.mswallet.repository.IWalletRepository;
import com.nttdata.bc19.mswallet.request.TransactionWalletRequest;
import com.nttdata.bc19.mswallet.request.WalletRequest;
import com.nttdata.bc19.mswallet.service.IWalletService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class WalletServiceImpl implements IWalletService{

    private final Logger LOGGER = LoggerFactory.getLogger("WalletLog");

    @Autowired
    IWalletRepository iWalletRepository;

    @Autowired
    ITransactionWalletRepository iTransactionWalletRepository;

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

    @Override
    public Mono<Wallet> findById(String id) {
        return iWalletRepository.findById(id);
    }

    @Override
    public Flux<Wallet> findAll() {
        return iWalletRepository.findAll();
    }

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
                                                    walletDestinyResponse.setAmount(walletDestinyResponse.getAmount() - walletDestinyResponse.getAmount());
                                                    walletDestinyResponse.setUpdatedAt(LocalDateTime.now());
                                                    return this.update(walletDestinyResponse)
                                                            .switchIfEmpty(Mono.error(new ModelNotFoundException("Id Wallet Not Found.")))
                                                            .flatMap(walletDestinyUpdateResponse -> {
                                                                TransactionWallet transactionWallet = new TransactionWallet();
                                                                transactionWallet.setId(new ObjectId().toString());
                                                                transactionWallet.setCreatedAt(LocalDateTime.now());
                                                                transactionWallet.setPhoneSource(transactionWalletRequest.getPhoneSource());
                                                                transactionWallet.setPhoneDestiny(transactionWalletRequest.getPhoneDestiny());
                                                                return iTransactionWalletRepository.save(transactionWallet);
                                                            });
                                                });
                                    });
                        });
    }
}
