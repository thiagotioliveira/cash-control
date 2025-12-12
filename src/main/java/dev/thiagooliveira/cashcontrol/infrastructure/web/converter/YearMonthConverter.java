package dev.thiagooliveira.cashcontrol.infrastructure.web.converter;

import java.time.YearMonth;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class YearMonthConverter implements Converter<String, YearMonth> {

  @Override
  public YearMonth convert(String source) {
    return YearMonth.parse(source);
  }
}
