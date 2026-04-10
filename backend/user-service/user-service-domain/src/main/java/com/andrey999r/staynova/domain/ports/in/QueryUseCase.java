package com.andrey999r.staynova.domain.ports.in;

public interface QueryUseCase<I, O> {
  O execute(I input);
}
