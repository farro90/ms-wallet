package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.RateCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface IRateCoinRepository extends ReactiveMongoRepository<RateCoin, String> {
}
