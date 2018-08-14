package ru.trixiean.forlabsschedule.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import ru.trixiean.forlabsschedule.GroupSelectActivity;
import ru.trixiean.forlabsschedule.R;

/**
 * Created by Trixiean on 07.05.2017.
 * https://play.google.com/store/apps/developer?id=Trixiean
 */

public class AdapterGroups extends RecyclerView.Adapter<AdapterGroups.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private static WeakReference<GroupSelectActivity> mParentActivityRef;

    public static void setFragment(GroupSelectActivity a) {
        mParentActivityRef = new WeakReference<GroupSelectActivity>(a);
    }

    private ArrayList<Group> mGroups = new ArrayList<>();

    public AdapterGroups(ArrayList<Group> mGroups) {
        this.mGroups = mGroups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View Root;
        if (viewType == TYPE_ITEM)
            Root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        else
            Root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_header, parent, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Root.setLayoutParams(lp);
        return new ViewHolder(Root);
    }

    @Override
    public void onBindViewHolder(final ViewHolder Holder, int position) {
        Holder.Title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//        My group.
//        if (mGroups.get(position).getName().equals("14221-ДБ")) {
//            Holder.Title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_favorite, 0);
//        }
        Holder.Title.setText(mGroups.get(position).getName());
        Holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!mGroups.get(Holder.getAdapterPosition()).isHeader()) {
                        if (mParentActivityRef.get() != null)
                            mParentActivityRef.get().onChooseGroup(mGroups.get(Holder.getAdapterPosition()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (mGroups.get(position).isHeader()) {
            LinearLayout.LayoutParams Params = new LinearLayout.LayoutParams(Holder.LinearLayoutHeader.getLayoutParams());
            if (Holder.getAdapterPosition() > 0)
                Params.setMargins(0, (int) LocalConverter.convertDpToPixel(Holder.itemView.getContext(), 16), 0, 0);
            Holder.LinearLayoutHeader.setLayoutParams(Params);
        }
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mGroups.get(position).isHeader() ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public boolean onFailedToRecycleView(ViewHolder holder) {
        return true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout LinearLayoutHeader;
        TextView Title;

        ViewHolder(View itemView) {
            super(itemView);
            Title = itemView.findViewById(R.id.GroupNameText);
            LinearLayoutHeader = itemView.findViewById(R.id.LinearLayoutHeader);
        }
    }
}
