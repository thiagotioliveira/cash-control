package dev.thiagooliveira.cashcontrol.infrastructure.web.manager;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormattersUtils {

  public static final ZoneId zoneId = ZoneId.of("Europe/Lisbon");
  public static final DecimalFormat df;
  public static final DateTimeFormatter dtf;
  public static final DateTimeFormatter dtfHourOfDay;

  static {
    df = (DecimalFormat) DecimalFormat.getInstance(Locale.of("pt", "PT"));
    df.applyPattern("#,##0.00");

    dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    dtfHourOfDay = DateTimeFormatter.ofPattern("dd/MM HH:mm");
  }
}
