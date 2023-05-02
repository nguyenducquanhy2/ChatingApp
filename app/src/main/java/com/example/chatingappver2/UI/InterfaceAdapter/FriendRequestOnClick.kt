import com.example.chatingappver2.Model.FriendRequest

interface FriendRequestOnClick {
    fun btnAcceptClickListener(friendRequest: FriendRequest)
    fun btnDeleteClickListener(idUserSendRequest:String)
}