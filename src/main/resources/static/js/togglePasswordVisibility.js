function togglePasswordVisibility(id) {
    var passwordInput = document.getElementById(id);
    var type = passwordInput.type === "password" ? "text" : "password";
    passwordInput.type = type;

    // Update the icon based on the input type
    var icon = document.getElementById("toggle-password-icon-" + id);
    if (type === "password") {
        icon.classList.remove("fa-eye-slash");
        icon.classList.add("fa-eye");
    } else {
        icon.classList.remove("fa-eye");
        icon.classList.add("fa-eye-slash");
    }
}