package com.example.cmpt276project.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.cmpt276project.R;
import com.example.cmpt276project.struct.RestaurantsManager;

public class SearchFilterDialog extends AppCompatDialogFragment {

    private Switch isFavourite;
    private Spinner hazardLevel;
    private EditText minViolations;

    private boolean isFavourited;
    private String hazard;
    private int minViolationsInt;

    private SearchFilterDialogListener listener;

    RestaurantsManager manager;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.search_filter, null);

        isFavourite = (Switch) view.findViewById(R.id.switch_filter_box_isFavourite);
        hazardLevel = (Spinner) view.findViewById(R.id.spinner_filter_box_hazard_level);
        minViolations = (EditText) view.findViewById(R.id.editText_filter_box_number_of_violations);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.hazard_levels, R.layout.support_simple_spinner_dropdown_item);
        hazardLevel.setPrompt("choose hazard level");
        hazardLevel.setAdapter(adapter);

        builder.setView(view)
                .setNegativeButton(getActivity().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Get the int value. if they select nothing it stays at 0
                        String minViolationsString = minViolations.getText().toString();
                        if(!minViolationsString.equals("")){
                            minViolationsInt = Integer.parseInt(minViolationsString);
                        }else{
                            minViolationsInt = 0;
                        }

                        //Get the value of the Switch as a boolean
                        isFavourited = isFavourite.isChecked();

                        hazard = hazardLevel.getSelectedItem().toString();

                        listener.getInput(isFavourited, hazard, minViolationsInt);

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (SearchFilterDialogListener) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ClassCastException(context.toString() + "must implement SearchFilterDialogListener");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public interface SearchFilterDialogListener{
        void getInput(boolean isFavourited, String hazard, int minViolationsInt);
    }
}
