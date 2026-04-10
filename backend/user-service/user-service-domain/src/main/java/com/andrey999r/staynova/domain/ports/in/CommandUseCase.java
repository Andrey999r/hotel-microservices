package com.andrey999r.staynova.domain.ports.in;

public interface CommandUseCase<I> {
  void execute(I input);
}
