package dev.thiagooliveira.cashcontrol.infrastructure.web.converter;

import dev.thiagooliveira.cashcontrol.shared.TransactionType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionTypeConverter implements Converter<String, TransactionType> {

  @Override
  public TransactionType convert(String source) {
    return TransactionType.valueOf(source);
  }
}
