package com.lingci.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.lingci.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

/**
 * Created by bafsj on 16/1/22.
 */
public class ContactsFragment extends Fragment{

    private RelativeLayout dletec;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        dletec = (RelativeLayout) view.findViewById(R.id.dletec);
        dletec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RongIM.getInstance().startConversation(getActivity(), Conversation.ConversationType.CHATROOM, "2016520", "大龄儿童二次同好");
            }
        });
    }
}
