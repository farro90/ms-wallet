package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.TransactionWallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ITransactionWalletRepository extends ReactiveMongoRepository<TransactionWallet, String> {
}
