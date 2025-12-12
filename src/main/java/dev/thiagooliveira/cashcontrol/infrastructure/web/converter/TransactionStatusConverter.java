package dev.thiagooliveira.cashcontrol.infrastructure.web.converter;

import dev.thiagooliveira.cashcontrol.shared.TransactionStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionStatusConverter implements Converter<String, TransactionStatus> {

  @Override
  public TransactionStatus convert(String source) {
    return TransactionStatus.valueOf(source);
  }
}
