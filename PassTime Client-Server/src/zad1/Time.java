
package zad1;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    public static String passed(String from, String to){

        StringBuilder stringBuilder = new StringBuilder();

        try{
            if(from.length()==10){


                LocalDate localDateFrom = LocalDate.parse(from);
                LocalDate localDateTo = LocalDate.parse(to);
                DateTimeFormatter formatterFrom = DateTimeFormatter.ofPattern("'Od' d MMMM yyyy '('EEEE')' ").withLocale(new Locale("pl", "PL"));
                DateTimeFormatter formatterTo = DateTimeFormatter.ofPattern("'do' d MMMM yyyy '('EEEE')' ").withLocale(new Locale("pl", "PL"));

                stringBuilder.append(localDateFrom.format(formatterFrom));
                stringBuilder.append(localDateTo.format(formatterTo));

                // mija
                stringBuilder.append('\n');
                stringBuilder.append(mija((int)ChronoUnit.DAYS.between(localDateFrom, localDateTo)));

                // kalendarzowo
                Period period = Period.between(localDateFrom, localDateTo);
                stringBuilder.append('\n');
                stringBuilder.append(kalendarzowo(period));




            } else if(from.length()==16){

                LocalDateTime localDateTimeFrom = LocalDateTime.parse(from);
                LocalDateTime localDateTimeTo = LocalDateTime.parse(to);

                DateTimeFormatter formatterFrom = DateTimeFormatter.ofPattern("'Od' d MMMM yyyy '('EEEE') godz.' k':'mm ").withLocale(new Locale("pl", "PL"));
                DateTimeFormatter formatterTo = DateTimeFormatter.ofPattern("'do' d MMMM yyyy '('EEEE') godz.' k':'mm ").withLocale(new Locale("pl", "PL"));

                stringBuilder.append(localDateTimeFrom.format(formatterFrom));
                stringBuilder.append(localDateTimeTo.format(formatterTo));


                ZonedDateTime zonedDateTimeFrom = ZonedDateTime.of(localDateTimeFrom, ZoneId.of("Europe/Warsaw"));
                ZonedDateTime zonedDateTimeTo = ZonedDateTime.of(localDateTimeTo, ZoneId.of("Europe/Warsaw"));



                // mija
                stringBuilder.append('\n');
                stringBuilder.append(mija(ChronoUnit.DAYS.between(localDateTimeFrom.toLocalDate(), localDateTimeTo.toLocalDate())));



                // godzin
                stringBuilder.append('\n');
                stringBuilder.append("- godzin: " + ChronoUnit.HOURS.between(zonedDateTimeFrom, zonedDateTimeTo) +
                        ", minut: " + ChronoUnit.MINUTES.between(zonedDateTimeFrom, zonedDateTimeTo));


                // kalendarzowo
                Period period = Period.between(localDateTimeFrom.toLocalDate(), localDateTimeTo.toLocalDate());
                if(period.getDays() != 0 || period.getMonths() != 0 || period.getYears() != 0){
                    stringBuilder.append('\n');
                    stringBuilder.append(kalendarzowo(period));
                }

            }
        } catch (DateTimeParseException e){
            stringBuilder.append("*** ");
            stringBuilder.append(e);
            return stringBuilder.toString();
        }




        return stringBuilder.toString();
    }


    private static String kalendarzowo(Period period){

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("- kalendarzowo: ");


        if(period.getYears()>0){
            stringBuilder.append(period.getYears());
            if(String.valueOf(period.getYears()).endsWith("1") && period.getYears()!=11){
                stringBuilder.append(" rok");
            } else if(String.valueOf(period.getYears()).endsWith("12") ||
                    String.valueOf(period.getYears()).endsWith("13") ||
                    String.valueOf(period.getYears()).endsWith("14")){
                stringBuilder.append(" lat");
            } else if(String.valueOf(period.getYears()).endsWith("2") ||
                    String.valueOf(period.getYears()).endsWith("3") ||
                    String.valueOf(period.getYears()).endsWith("4")){
                stringBuilder.append(" lata");
            } else {
                stringBuilder.append(" lat");
            }
        }



        if(period.getMonths()>0){
            if(period.getYears()>0)
                stringBuilder.append(", ");
            stringBuilder.append(period.getMonths());
            if(period.getMonths()==1){
                stringBuilder.append(" miesiąc");
            } else if (String.valueOf(period.getMonths()).endsWith("2") ||
                    String.valueOf(period.getMonths()).endsWith("3")||
                    String.valueOf(period.getMonths()).endsWith("4")){
                stringBuilder.append(" miesiące");
            } else {
                stringBuilder.append(" miesięcy");
            }
        }


        if(period.getDays()>0){
            if(period.getMonths()>0 || period.getYears()>0)
                stringBuilder.append(", ");
            stringBuilder.append(period.getDays());
            if(period.getDays()==1){
                stringBuilder.append(" dzień");
            } else {
                stringBuilder.append(" dni");
            }
        }


        return stringBuilder.toString();
    }


    private static String mija(long days){

        StringBuilder stringBuilder = new StringBuilder();
        double weeks = (double) Math.round((((double) days / 7) * 100)) /100;

        stringBuilder.append("- mija: ");
        stringBuilder.append(days);
        if(days == 1){
            stringBuilder.append(" dzień, ");
        } else {
            stringBuilder.append(" dni, ");
        }

        stringBuilder.append("tygodni ");
        stringBuilder.append(weeks);

        return stringBuilder.toString();
    }
}