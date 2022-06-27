package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.ClientCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ICoinRepository extends ReactiveMongoRepository<ClientCoin, String> {
    Mono<ClientCoin> findByPhone(String phone);
}
