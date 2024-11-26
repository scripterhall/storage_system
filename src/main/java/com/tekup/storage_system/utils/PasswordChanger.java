package com.tekup.storage_system.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChanger {

    private String currentPassword;
    private String newPassword;
    private String comfirmPassword;

    public boolean isPasswordsMatch() {
        return newPassword.equals(comfirmPassword);
    }

}
