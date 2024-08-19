document.getElementById("menu-icon").onclick = function() {
    this.classList.toggle("change");
    var dropdownMenu = document.getElementById("dropdown-menu");
    dropdownMenu.classList.toggle("show");
};
