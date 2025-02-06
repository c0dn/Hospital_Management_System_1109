package humans;

public class Contact {
    private String personalPhone;
    private String homePhone;
    private String companyPhone;
    private String email;

    public Contact(String personalPhone, String homePhone, String companyPhone, String email) {
        this.personalPhone = personalPhone;
        this.homePhone = homePhone;
        this.companyPhone = companyPhone;
        this.email = email;
    }

    public String getPersonalPhone() {
        return personalPhone;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public String getEmail() {
        return email;
    }


}
