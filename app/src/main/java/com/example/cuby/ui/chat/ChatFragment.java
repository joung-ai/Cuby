package com.example.cuby.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cuby.R;

public class ChatFragment extends Fragment {

    private ChatViewModel viewModel;
    private ChatAdapter adapter;
    private RecyclerView recyclerView;
    private EditText etMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = view.findViewById(R.id.toolbarTitleText);
        ImageView icon = view.findViewById(R.id.toolbarIcon);
        View backBtn = view.findViewById(R.id.btnBack);

        title.setText("Chat with Cuby");

        icon.setImageResource(R.drawable.cuby_idle);
        icon.setVisibility(View.VISIBLE);

        backBtn.findViewById(R.id.btnBack)
                .setOnClickListener(v ->
                        requireActivity()
                                .getOnBackPressedDispatcher()
                                .onBackPressed()
                );

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        recyclerView = view.findViewById(R.id.recyclerView);
        etMessage = view.findViewById(R.id.etMessage);



        adapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        view.findViewById(R.id.btnSend).setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                viewModel.sendMessage(text);
                etMessage.setText("");
            }
        });

        viewModel.getMessages().observe(getViewLifecycleOwner(), msgs -> {
            adapter.setMessages(msgs);
            if (!msgs.isEmpty()) {
                recyclerView.scrollToPosition(msgs.size() - 1);
            }
        });
    }
}
