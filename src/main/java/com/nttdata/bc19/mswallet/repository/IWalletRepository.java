package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IWalletRepository extends ReactiveMongoRepository<Wallet, String> {
    Mono<Wallet> findByPhone(String phone);
}
