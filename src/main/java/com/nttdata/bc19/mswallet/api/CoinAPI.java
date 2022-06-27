package com.nttdata.bc19.mswallet.api;

import com.nttdata.bc19.mswallet.model.ClientCoin;
import com.nttdata.bc19.mswallet.model.RateCoin;
import com.nttdata.bc19.mswallet.model.RequestBuyCoin;
import com.nttdata.bc19.mswallet.model.TransactionCoin;
import com.nttdata.bc19.mswallet.request.AcceptRequestBuyCoinRequest;
import com.nttdata.bc19.mswallet.request.CoinRequest;
import com.nttdata.bc19.mswallet.request.RequestBuyCoinRequest;
import com.nttdata.bc19.mswallet.service.ICoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/coin")
public class CoinAPI {

    @Autowired
    private ICoinService coinService;

    @PostMapping
    public Mono<ClientCoin> create(@RequestBody CoinRequest coinRequest){ return coinService.create(coinRequest); }

    @PutMapping
    public Mono<ClientCoin> update(@RequestBody ClientCoin clientCoin){ return coinService.update(clientCoin); }

    @GetMapping
    public Flux<ClientCoin> findAll(){
        return coinService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ClientCoin> findById(@PathVariable String id){ return coinService.findById(id); }

    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id){
        return coinService.deleteById(id);
    }

    @GetMapping("/rate")
    public Flux<RateCoin> getRate(){
        return coinService.getRate();
    }

    @PostMapping("/rate")
    public Mono<RateCoin> create(@RequestBody RateCoin rateCoin){ return coinService.setRateCoin(rateCoin); }

    @PostMapping("/request-buy-coin")
    public Mono<RequestBuyCoin> requestBuyCoin(@RequestBody RequestBuyCoinRequest requestBuyCoin){ return coinService.requestBuyCoin(requestBuyCoin); }

    @GetMapping("/request-buy-coin")
    public Flux<RequestBuyCoin> findAllRequest(){ return coinService.findAllRequestBuyCoin(); }

    @PostMapping("/accept-buy-coin")
    public Mono<TransactionCoin> acceptBuyCoin(@RequestBody AcceptRequestBuyCoinRequest acceptRequest){ return coinService.acceptRequestBuyCoin(acceptRequest); }
}
