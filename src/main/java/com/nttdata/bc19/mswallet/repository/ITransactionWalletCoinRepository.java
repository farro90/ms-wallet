package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.TransactionWalletCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ITransactionWalletCoinRepository extends ReactiveMongoRepository<TransactionWalletCoin, String> {
}
