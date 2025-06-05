package com.example.cdd.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cdd.Controller.LoginController;
import com.example.cdd.R;

import java.util.List;

public class TopFragment extends Fragment implements View.OnClickListener {

    private FrameLayout fragmentContainer;
    private ImageView btnBack;
    private RecyclerView recyclerView;
    private LoginController loginController;
    private List<List<String>> rankList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        initView(view);
        initData();
        return view;
    }

    protected void initView(View view) {
        btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recycler_top_list);
    }

    private void initData() {
        loginController = new LoginController(requireContext());
        rankList = loginController.getAllUserScore();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new RankAdapter(rankList));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_back){
            backToMain();
        }
    }

    private void backToMain() {
        if (fragmentContainer == null) {
            fragmentContainer = getActivity().findViewById(R.id.framelayout);
        }
        if (fragmentContainer != null) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            if (getActivity() != null) getActivity().finish();
            fragmentContainer.setClickable(false);
            fragmentContainer.setVisibility(View.INVISIBLE);
        }
    }

    private class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

        private final List<List<String>> data;

        public RankAdapter(List<List<String>> data) {
            this.data = data;
        }

        @NonNull
        @Override
        public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_rank, parent, false);
            return new RankViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
            List<String> item = data.get(position);
            holder.tvRank.setText(String.valueOf(position + 1));
            holder.tvUsername.setText(item.get(0));
            holder.tvScore.setText(item.get(1));
        }

        @Override
        public int getItemCount() {
            return data == null ? 0 : data.size();
        }

        class RankViewHolder extends RecyclerView.ViewHolder {
            TextView tvRank, tvUsername, tvScore;

            public RankViewHolder(@NonNull View itemView) {
                super(itemView);
                tvRank = itemView.findViewById(R.id.tv_rank);
                tvUsername = itemView.findViewById(R.id.tv_username);
                tvScore = itemView.findViewById(R.id.tv_score);
            }
        }
    }
}
