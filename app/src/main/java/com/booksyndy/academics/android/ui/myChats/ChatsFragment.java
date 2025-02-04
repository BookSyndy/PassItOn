package com.booksyndy.academics.android.ui.myChats;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.booksyndy.academics.android.Adapters.ChatsAdapter;
import com.booksyndy.academics.android.ChatActivity;
import com.booksyndy.academics.android.Data.Chat;
import com.booksyndy.academics.android.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatsFragment extends Fragment implements EventListener<QuerySnapshot>, ChatsAdapter.OnChatSelectedListener {

    private static final String TAG = "CHAT_FRAGMENT";
    private RecyclerView recyclerView;
    private ViewGroup mEmptyView;
    private CollectionReference ChatsRef;
    private ChatsAdapter mAdapter;
    private List<Chat> chatList;
    private List<Chat> chatListFull;
    private ListenerRegistration chatsRegistration;


    public ChatsFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel = ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View privateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);
        setHasOptionsMenu(true);
        recyclerView = privateChatsView.findViewById(R.id.chat_recycler_view);
        mEmptyView = privateChatsView.findViewById(R.id.chat_view_empty);
        initFireStore();

        chatList = new ArrayList<>();
        chatListFull = new ArrayList<>();
        mAdapter = new ChatsAdapter(getContext(), chatList, this) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                Log.d(TAG, "onDataChanged: entered");
                if (getItemCount() == 0) {
                    Log.d(TAG, "onDataChanged: empty");
                    recyclerView.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {

                    recyclerView.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
            }
        };

/*        Toast.makeText(getActivity(), "Toast works", Toast.LENGTH_SHORT).show();

        if (mAdapter.hasUnreadChat()) {
            Toast.makeText(getActivity(), "User has an unread chat", Toast.LENGTH_SHORT).show();
        }*/
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        return privateChatsView;
    }

    private void initFireStore() {
        try {
            /* firestore */
            FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String currentUserID = mAuth.getCurrentUser().getPhoneNumber();
            ChatsRef = mFireStore.collection("chats").document(currentUserID).collection("receiver_chats");
            chatsRegistration = ChatsRef.addSnapshotListener(this);

        } catch (Exception e) {
            Log.e(TAG, "initFireStore: failed", e);
        }
    }

    public void setupChatAdapter(QuerySnapshot queryDocumentSnapshots) {

        if (!queryDocumentSnapshots.isEmpty()) {
            chatList.clear();
            chatList.addAll(queryDocumentSnapshots.toObjects(Chat.class));
            //sort here
            Collections.sort(chatList, new Comparator<Chat>() {
                public int compare(Chat o1, Chat o2) {
                    if (o1.getTimeDiff() == -1|| o2.getTimeDiff() == -1)
                        return 0;
                    if(o1.getTimeDiff() > o2.getTimeDiff()){
                        return 1;
                    }
                    else
                        return -1;
                }
            });
            for (Chat chat:chatList){
                Log.d(TAG, chat.getDocumentId()+" time: "+chat.getTimeDiff());

            }
            mAdapter.setChatList(chatList);
            chatListFull = new ArrayList<>(chatList);
            mAdapter.onDataChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (chatsRegistration == null) {
            chatsRegistration = ChatsRef.addSnapshotListener(this);
            mAdapter.onDataChanged();
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.onDataChanged();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatsRegistration != null) {
            chatsRegistration.remove();
            chatsRegistration = null;
            mAdapter.onDataChanged();
        }

    }


    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.d(TAG, "onEvent: chats error", e);
            return;
        }
        setupChatAdapter(queryDocumentSnapshots);
    }

    @Override
    public void OnChatSelected(Chat chat) {
        if (chat.hasAllFields()) {
            //Toast.makeText(getContext(),"selectd :"+chat.getUserName(),Toast.LENGTH_SHORT).show();
            Intent chatIntent = new Intent(getContext(), ChatActivity.class);
            chatIntent.putExtra("visit_user_id", chat.getUserId());
            chatIntent.putExtra("visit_user_name", chat.getUserName());
            chatIntent.putExtra("visit_image", chat.getImageUrl());
            startActivity(chatIntent);
        } else {
            Snackbar.make(getView(), "Internal Error Occurred", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);

        menu.findItem(R.id.filter).setVisible(false);
//        menu.findItem(R.id.open_chats).setVisible(false);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<Chat> filteredList = new ArrayList<>();
                if (newText == null || newText.trim().length() == 0) {
                    filteredList = chatListFull;
                    searchView.clearFocus();
                } else {
                    String filterPattern = newText.toLowerCase().trim();
                    for (Chat chat : chatListFull) {
                        if (chat.getUserName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(chat);
                        }
                    }
                }
                mAdapter.setChatList(filteredList);
                return false;
            }
        });

        searchItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                searchView.onActionViewExpanded();
                searchView.requestFocus();
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}