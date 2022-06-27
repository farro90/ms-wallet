package com.nttdata.bc19.mswallet.service;

import com.nttdata.bc19.mswallet.model.ClientCoin;
import com.nttdata.bc19.mswallet.model.RateCoin;
import com.nttdata.bc19.mswallet.model.RequestBuyCoin;
import com.nttdata.bc19.mswallet.model.TransactionCoin;
import com.nttdata.bc19.mswallet.request.AcceptRequestBuyCoinRequest;
import com.nttdata.bc19.mswallet.request.CoinRequest;
import com.nttdata.bc19.mswallet.request.RequestBuyCoinRequest;
import com.nttdata.bc19.mswallet.request.TransactionWalletRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICoinService {
    Mono<ClientCoin> create(CoinRequest coinRequest);
    Mono<ClientCoin> update(ClientCoin clientCoin);
    Mono<Void>deleteById(String id);
    Mono<ClientCoin> findById(String id);
    Flux<ClientCoin> findAll();

    Flux<RateCoin> getRate();
    Mono<RateCoin> setRateCoin(RateCoin rateCoin);
    Mono<RequestBuyCoin> requestBuyCoin(RequestBuyCoinRequest requestBuyCoinRequest);
    Flux<RequestBuyCoin> findAllRequestBuyCoin();

    Mono<TransactionCoin> acceptRequestBuyCoin(AcceptRequestBuyCoinRequest acceptRequestBuyCoinRequest);
}
