sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Post : Screen("post")
    object Map : Screen("map")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object EditPost : Screen("edit_post")
    object PostDetail : Screen("post_detail")
    object Login : Screen("login")
    object Signup : Screen("signup")
}