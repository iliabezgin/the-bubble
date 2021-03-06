package huji.postpc.y2021.tal.yichye.thebubble;


import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.firestore.FirebaseFirestore;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.ChatInfo;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.Message;
import huji.postpc.y2021.tal.yichye.thebubble.Connections.Request;

public class UsersDB {

	private FirebaseFirestore db = null;

	public UsersDB(Context context) {
		db = FirebaseFirestore.getInstance();
	}


	public LiveData<PersonData> getUserByID(String userId)
	{
		MutableLiveData<PersonData> liveData = new MutableLiveData<>();
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						liveData.setValue(user);
					}
					else {
						liveData.setValue(null);
					}
				});
		return liveData;
	}

	public void addUserToDB(PersonData userToSave){
		FirebaseFirestore db = FirebaseFirestore.getInstance();
		db.collection("users").document(userToSave.getId()).set(userToSave);
	}


	private void removeUserFromDB(String id){
		db.collection("users").document(id).delete();
	}

	public void updateUserField(String userId, String fieldToChange, Object newValue)
	{
		db.collection("users").document(userId).update(fieldToChange, newValue);

	}

	public void addRequest(String userId, Request newRequest)
	{
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						user.requests.add(newRequest);
						updateUserField(userId, "requests", user.requests);
					}
				});
	}


	public void updateChatInfoByIdAndMsg(Message message, String idChatWith, String idSelf) {
		db.collection("users").document(idSelf).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						for (int i = 0; i < user.chatInfos.size(); i++) {
							ChatInfo c = user.chatInfos.get(i);
							if (c.getChatWith().equals(idChatWith)) {
								c.setLastSentMsg(message.getContent());
								c.setTimeLastSentMsg(message.getTimeSent());
								c.setDateLastSentMsg(message.getDateSent());
							}
						}
						updateUserField(idSelf, "chatInfos", user.chatInfos);
					} else {
						Log.d("users db", "could not find matching chat info");
					}
				});
	}

	public void addToIgnoreList(String userId, String ignoredUserId) {
		db.collection("users").document(userId).get()
				.addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						PersonData user = documentSnapshot.toObject(PersonData.class);
						user.ignoreList.add(ignoredUserId);
						updateUserField(userId, "ignoreList", user.ignoreList);
					}
				});
	}

	public FirebaseFirestore getDb() {
		return db;
	}

}
