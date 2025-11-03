package login;

public class MemberModel {
    private final String memberId;
    private final String firstName;
    private final String lastName;
    private final String username;

    public MemberModel(String memberId, String firstName, String lastName, String username) {
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    public String getMemberId() { return memberId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUsername() { return username; }
}
