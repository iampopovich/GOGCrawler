package com.example.gogcrawler.domain.usecases;

public abstract class UseCase<P, R> {
    protected interface OnSuccessListener<R> {
        void onSuccess(R result);
    }

    protected interface OnErrorListener {
        void onError(String error);
    }
}
