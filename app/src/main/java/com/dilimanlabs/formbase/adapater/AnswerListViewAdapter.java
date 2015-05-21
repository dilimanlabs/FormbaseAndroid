package com.dilimanlabs.formbase.adapater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;
import com.dilimanlabs.formbase.model.AnswersForApproval;

import java.util.List;

/**
 * Created by user on 5/14/2015.
 */
public class AnswerListViewAdapter extends RecyclerView.Adapter<AnswerListViewAdapter.ViewHolder> {
    private List<AnswersForApproval> answersForApprovalList;
    private int rowLayout;
    private Context mContext;

    public AnswerListViewAdapter(List<AnswersForApproval> answersForApprovalList, int rowLayout, Context context) {
        this.answersForApprovalList = answersForApprovalList;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        AnswersForApproval answersForApproval = answersForApprovalList.get(i);
        viewHolder.submitted_by.setText(answersForApproval.getCreated_by());
        viewHolder.state.setText(answersForApproval.getState());
    }

    @Override
    public int getItemCount() {
        return answersForApprovalList == null ? 0 : answersForApprovalList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView submitted_by;
        public TextView state;


        public ViewHolder(View itemView) {
            super(itemView);
            submitted_by = (TextView) itemView.findViewById(R.id.submitted_by);
            state = (TextView) itemView.findViewById(R.id.state);
        }

    }
}
