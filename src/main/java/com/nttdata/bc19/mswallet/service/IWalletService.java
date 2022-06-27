package com.nttdata.bc19.mswallet.service;

import com.nttdata.bc19.mswallet.model.TransactionWallet;
import com.nttdata.bc19.mswallet.model.TransactionWalletCoin;
import com.nttdata.bc19.mswallet.model.Wallet;
import com.nttdata.bc19.mswallet.request.TransactionWalletCoinRequest;
import com.nttdata.bc19.mswallet.request.TransactionWalletRequest;
import com.nttdata.bc19.mswallet.request.WalletRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IWalletService {
    Mono<Wallet> create(WalletRequest walletRequest);
    Mono<Wallet> update(Wallet wallet);
    Mono<Void>deleteById(String id);
    Mono<Wallet> findById(String id);
    Flux<Wallet> findAll();

    Mono<TransactionWallet> transactionWallet(TransactionWalletRequest transactionWalletRequest);
    Mono<TransactionWalletCoin> transactionWalletCoin(TransactionWalletCoinRequest transactionWalletCoinRequest);
}
