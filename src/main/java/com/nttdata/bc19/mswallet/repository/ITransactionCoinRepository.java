package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.TransactionCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ITransactionCoinRepository extends ReactiveMongoRepository<TransactionCoin, String> {
}
