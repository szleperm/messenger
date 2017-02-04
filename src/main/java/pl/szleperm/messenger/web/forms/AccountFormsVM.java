package pl.szleperm.messenger.web.forms;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Marcin Szleper
 */
public abstract class AccountFormsVM {

    public static class RegisterFormVM {
        @NotEmpty
        @Size(min = 3, max = 50)
        private String username;
        @NotEmpty
        @Email
        @Size(min = 3, max = 50)
        private String email;
        @NotEmpty
        @Size(min = 3, max = 50)
        private String password;
        private String confirmPassword;

        public RegisterFormVM() {
        }

        public RegisterFormVM(String username, String email, String password, String confirmPassword) {
            super();
            this.username = username;
            this.email = email;
            this.password = password;
            this.confirmPassword = confirmPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    @SuppressWarnings("unused")
    public static class ChangePasswordFormVM {
        @NotEmpty
        private String username;
        @NotEmpty
        private String oldPassword;
        @NotNull
        @Size(min = 3, max = 50)
        private String newPassword;
        private String confirmNewPassword;

        public ChangePasswordFormVM() {
        }

        public ChangePasswordFormVM(String username, String oldPassword, String newPassword, String confirmNewPassword) {
            super();
            this.username = username;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            this.confirmNewPassword = confirmNewPassword;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmNewPassword() {
            return confirmNewPassword;
        }

        public void setConfirmNewPassword(String confirmNewPassword) {
            this.confirmNewPassword = confirmNewPassword;
        }
    }
}
