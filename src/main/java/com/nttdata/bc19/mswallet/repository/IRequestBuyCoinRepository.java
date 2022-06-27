package com.nttdata.bc19.mswallet.repository;

import com.nttdata.bc19.mswallet.model.RequestBuyCoin;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface IRequestBuyCoinRepository extends ReactiveMongoRepository<RequestBuyCoin, String> {

}
