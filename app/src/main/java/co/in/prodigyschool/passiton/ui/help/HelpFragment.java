package co.in.prodigyschool.passiton.ui.help;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import co.in.prodigyschool.passiton.R;

public class HelpFragment extends Fragment {

    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> questionsList,answersList;
    private HashMap<String,String> hashMap;
    private int cc = 1;
    /*
    todo: add or remove question here
    private String[] questions = new String[]{"How does BookSyndy work?","question 2","question 3"};
    private String[] answers = new String[]{"BookSyndy works like charm.","answer 2","answer 3"};
*/
    private String[] questions;
    private String[] answers;

    private ShareViewModel shareViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        shareViewModel =
                ViewModelProviders.of(this).get(ShareViewModel.class);
        View root = inflater.inflate(R.layout.fragment_share, container, false);
        expandableListView = root.findViewById(R.id.expandable_list_faq);
        initData();
        expandableListAdapter = new FaqAdapter(getContext(),questionsList,hashMap);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView arrow = view.findViewById(R.id.dropdownDummyButton);
                arrow.animate().rotation(cc*180).setDuration(100);
                cc = cc+1;
            }
        });
        return root;
    }

    private void initData() {
        questionsList = new ArrayList<>();
        answersList = new ArrayList<>();
        hashMap = new HashMap<>();
        questions = getResources().getStringArray(R.array.questions);
        answers = getResources().getStringArray(R.array.answers);
        questionsList = Arrays.asList(questions);
        answersList = Arrays.asList(answers);
        for(int i=0;i<questionsList.size();i++){
            hashMap.put(questionsList.get(i),"\n"+answersList.get(i)+"\n");
        }
    }
}