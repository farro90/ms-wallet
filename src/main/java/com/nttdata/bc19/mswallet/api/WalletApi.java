package com.nttdata.bc19.mswallet.api;

import com.nttdata.bc19.mswallet.model.TransactionWallet;
import com.nttdata.bc19.mswallet.model.Wallet;
import com.nttdata.bc19.mswallet.request.TransactionWalletRequest;
import com.nttdata.bc19.mswallet.request.WalletRequest;
import com.nttdata.bc19.mswallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/wallet")
public class WalletApi {
    @Autowired
    private IWalletService walletService;

    @PostMapping
    public Mono<Wallet> create(@RequestBody WalletRequest walletRequest){ return walletService.create(walletRequest); }

    @PutMapping
    public Mono<Wallet> update(@RequestBody Wallet wallet){ return walletService.update(wallet); }

    @GetMapping
    public Flux<Wallet> findAll(){
        return walletService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Wallet> findById(@PathVariable String id){ return walletService.findById(id); }
    
    @DeleteMapping("/{id}")
    public Mono<Void> delete(@PathVariable String id){
        return walletService.deleteById(id);
    }

    @PostMapping("/transaction")
    public Mono<TransactionWallet> transactionWallet(@RequestBody TransactionWalletRequest transactionWalletRequest){
        return walletService.transactionWallet(transactionWalletRequest);
    }
}
