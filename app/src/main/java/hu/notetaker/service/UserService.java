package hu.notetaker.service;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Optional;

import hu.notetaker.Md5Util;
import hu.notetaker.binding.UserBinding;

public final class UserService {

    public static Optional<UserBinding> getUser() {
        var mAuth = FirebaseAuth.getInstance();
        var user = mAuth.getCurrentUser();

        if (user == null) {
            return Optional.empty();
        }

        var email = user.getEmail();
        var hash = Md5Util.MD5_Hash(email);
        var gravatarUrl = "https://gravatar.com/avatar/" + hash + "?s=204&d=retro";

        var userBinding = new UserBinding();
        userBinding.setEmailAddress(email);
        userBinding.setAvatarUrl(gravatarUrl);

        return Optional.of(userBinding);
    }

}
