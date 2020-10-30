package edu.csce4623.ahnelson.todomvp3.todolistactivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.csce4623.ahnelson.todomvp3.R;
import edu.csce4623.ahnelson.todomvp3.data.ToDoItem;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ToDoListFragment implements the ToDoListContract.View class.
 * Populates into ToDoListActivity content frame
 */
public class ToDoListFragment extends Fragment implements ToDoListContract.View {

    // Presenter instance for view
    private ToDoListContract.Presenter mPresenter;
    // Inner class instance for ListView adapter
    private ToDoItemsAdapter mToDoItemsAdapter;

    public ToDoListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ToDoListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ToDoListFragment newInstance() {
        ToDoListFragment fragment = new ToDoListFragment();
        return fragment;
    }

    /**
     * When fragment is created, create new instance of ToDoItemsAdapter with empty ArrayList and static ToDoItemsListener
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToDoItemsAdapter = new ToDoItemsAdapter(new ArrayList<ToDoItem>(0),mToDoItemsListener);
    }

    /**
     * start presenter during onResume
     * Ideally coupled with stopping during onPause (not needed here)
     */
    @Override
    public void onResume(){
        super.onResume();
        mPresenter.start();
    }

    /**
     * onCreateView inflates the fragment, finds the ListView and Button, returns the root view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return root view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_to_do_list, container, false);

        // Set up tasks view
        ListView listView = (ListView) root.findViewById(R.id.rvToDoList);
        listView.setAdapter(mToDoItemsAdapter);
        //Find button and set onClickMethod to add a New ToDoItem
        root.findViewById(R.id.btnNewToDo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addNewToDoItem();
            }
        });
        return root;
    }

    /**
     * set the presenter for this view
     * @param presenter - the ToDoListContract.presenter instance
     */
    @Override
    public void setPresenter(ToDoListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    /**
     * Replace the items in the ToDoItemsAdapter
     * @param toDoItemList - List of ToDoItems
     */
    @Override
    public void showToDoItems(List<ToDoItem> toDoItemList) {
        mToDoItemsAdapter.replaceData(toDoItemList);

    }

    // function used to receive context from the view
    public Context getContext(){
        return getActivity();
    }

    /**
     * Create intent to start ACTIVITY TO BE IMPLEMENTED!
     * Start the activity for result - callback is onActivityResult
     * @param item - Item to be added/modified
     * @param requestCode - Integer code referencing whether a ToDoItem is being added or edited
     */
    @Override
    public void showAddEditToDoItem(ToDoItem item, int requestCode) {
        // If requestCode is 0, the user clicked the button to create a new ToDoItem
        // If the requestCode is not 0, a ToDoItem in the list was selected for viewing/modifying

        Intent i = new Intent(getActivity(), SecondActivity.class);
        i.putExtra("toDoItem", item);

        startActivityForResult(i, requestCode);
    }

    /**
     * callback function for startActivityForResult
     * Data intent should contain a ToDoItem
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Check to make sure data object has a toDoItem
        assert data != null;
        if(data.hasExtra("ToDoItem")) {
            // check if the task is to be deleted
            if(data.hasExtra("DELETE")){
                requestCode = 2;
                mPresenter.result(requestCode, resultCode,(ToDoItem)data.getSerializableExtra("ToDoItem") );
            }
            else {
                mPresenter.result(requestCode, resultCode, (ToDoItem) data.getSerializableExtra("ToDoItem"));
            }
        }
    }

    /**
     * instance of ToDoItemsListener with onToDoItemClick function
     */
    ToDoItemsListener mToDoItemsListener = new ToDoItemsListener() {
        @Override
        public void onToDoItemClick(ToDoItem clickedToDoItem) {
            Log.d("FRAGMENT","Open ToDoItem Details");
            //Grab item from the ListView click and pass to presenter
            mPresenter.showExistingToDoItem(clickedToDoItem);
        }
    };

    /**
     * Adapter for ListView to show ToDoItems
     */
    private static class ToDoItemsAdapter extends BaseAdapter {

        //List of all ToDoItems
        private List<ToDoItem> mToDoItems;
        // Listener for onItemClick events
        private ToDoItemsListener mItemListener;

        /**
         * Constructor for the adapter
         * @param toDoItems - List of initial items
         * @param itemListener - onItemClick listener
         */
        public ToDoItemsAdapter(List<ToDoItem> toDoItems, ToDoItemsListener itemListener) {
            setList(toDoItems);
            mItemListener = itemListener;
        }

        /**
         * replace toDoItems list with new list
         * @param toDoItems
         */
        public void replaceData(List<ToDoItem> toDoItems) {
            setList(toDoItems);
            notifyDataSetChanged();
        }

        private void setList(List<ToDoItem> toDoItems) {
            mToDoItems = checkNotNull(toDoItems);
        }

        @Override
        public int getCount() {
            return mToDoItems.size();
        }

        @Override
        public ToDoItem getItem(int i) {
            return mToDoItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        /**
         * Get a View based on an index and viewgroup and populate
         * @param i
         * @param view
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.to_do_item_layout, viewGroup, false);
            }

            //get the ToDoItem associated with a given view
            //used in the OnItemClick callback
            final ToDoItem toDoItem = getItem(i);

            TextView titleTV = (TextView) rowView.findViewById(R.id.etItemTitle);
            titleTV.setText(toDoItem.getTitle());

            TextView contentTV = (TextView) rowView.findViewById(R.id.etItemContent);
            contentTV.setText(toDoItem.getContent());

            // if the toDoItem is marked as completed, the color of the text is changed to green
            if(toDoItem.getCompleted()){
                titleTV.setTextColor(Color.parseColor("#00ff00"));
                contentTV.setTextColor(Color.parseColor("#00ff00"));
            }
            // if not completed, text color is black
            else{
                titleTV.setTextColor(Color.parseColor("#000000"));
                contentTV.setTextColor(Color.parseColor("#000000"));
            }

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Set onItemClick listener
                    mItemListener.onToDoItemClick(toDoItem);
                }
            });
            return rowView;
        }
    }

    public interface ToDoItemsListener {
        void onToDoItemClick(ToDoItem clickedToDoItem);
    }
}