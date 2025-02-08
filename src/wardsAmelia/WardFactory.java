package wardsAmelia;

public class WardFactory {

    public static Ward getWard(String name, String wardType, WardClass wardClass) {
//        Ward ward = null;
        WardClassRate rate = WardClassRate.getRate(wardType, wardClass);

        if (wardType == null || wardClass == null) {
            return null;
        }

        switch (wardType.toUpperCase()) {
            case "LABOUR":
                return new LabourWard(name, wardClass, rate.getDailyRate());
            case "ICU":
                return new ICUWard(name, wardClass, rate.getDailyRate());
            case "DAY SURGERY":
                return new DaySurgeryWard(name, wardClass, rate.getDailyRate());
            case "GENERAL":
                return new GeneralWard(name, wardClass, rate.getDailyRate());
            default:
                throw new IllegalArgumentException("Invalid ward type.");
        }
    }
}
