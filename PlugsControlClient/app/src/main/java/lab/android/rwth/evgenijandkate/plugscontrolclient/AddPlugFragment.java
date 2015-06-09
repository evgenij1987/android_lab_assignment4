package lab.android.rwth.evgenijandkate.plugscontrolclient;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lab.android.rwth.evgenijandkate.plugscontrolclient.model.PlugTransferableData;
import lab.android.rwth.evgenijandkate.plugscontrolclient.model.StateEnum;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.AddPlugRequest;
import lab.android.rwth.evgenijandkate.plugscontrolclient.tasks.OnResponseListener;

/**
 * Created by ekaterina on 07.06.2015.
 *
 * The fragment for adding a new plug.
 * This functionality will be available only if the user has administrative rights.
 */
public class AddPlugFragment extends Fragment {
    private static final int DIGIT_MAX_VALUE = 1;
    private static final int DIGIT_MIN_VALUE = 0;
    private AddPlugRequest addPlugRequest;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retain state on configuration change (e.g. on orientation change)
        setRetainInstance(true);
    }

    /**
     * A screen for adding a new plug will be inflated only if the user has administrative rights.
     * This view consists of the entry for the plug's human readable name, the numberic pickers
     * from which the user can construct the switch and house codes (the range is restricted to 0 and 1 only)
     * and the initial state of the plug.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state.
     * @return a fragment's view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_plugs_fragment, container, false);
        final List<NumberPicker> switchDigits = new ArrayList<>();

        switchDigits.add((NumberPicker) view.findViewById(R.id.switch_digit_1));
        switchDigits.add((NumberPicker) view.findViewById(R.id.switch_digit_2));
        switchDigits.add((NumberPicker) view.findViewById(R.id.switch_digit_3));
        switchDigits.add((NumberPicker) view.findViewById(R.id.switch_digit_4));
        switchDigits.add((NumberPicker) view.findViewById(R.id.switch_digit_5));

        for (NumberPicker picker : switchDigits) {
            picker.setMaxValue(DIGIT_MAX_VALUE);
            picker.setMinValue(DIGIT_MIN_VALUE);
        }

        final List<NumberPicker> homeDigits = new ArrayList<>();

        homeDigits.add((NumberPicker) view.findViewById(R.id.house_digit_1));
        homeDigits.add((NumberPicker) view.findViewById(R.id.house_digit_2));
        homeDigits.add((NumberPicker) view.findViewById(R.id.house_digit_3));
        homeDigits.add((NumberPicker) view.findViewById(R.id.house_digit_4));
        homeDigits.add((NumberPicker) view.findViewById(R.id.house_digit_5));

        for (NumberPicker picker : homeDigits) {
            picker.setMaxValue(DIGIT_MAX_VALUE);
            picker.setMinValue(DIGIT_MIN_VALUE);
        }

        final EditText plugName = (EditText) view.findViewById(R.id.new_plug_name);
        StateEnum[] stateValues = StateEnum.values();
        String[] arraySpinner = new String[stateValues.length];
        for (int i = 0; i < stateValues.length; i++) {
            arraySpinner[i] = stateValues[i].getName();
        }
        final Spinner stateSpinner = (Spinner) view.findViewById(R.id.create_plug_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, arraySpinner);
        stateSpinner.setAdapter(adapter);

        Button addButton = (Button) view.findViewById(R.id.create_plug_from_activity);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer switchCode = new StringBuffer();
                for (NumberPicker picker : switchDigits) {
                    switchCode.append(picker.getValue());
                }

                StringBuffer houseCode = new StringBuffer();
                for (NumberPicker picker : homeDigits) {
                    houseCode.append(picker.getValue());
                }
                PlugTransferableData plugTransferableData = new PlugTransferableData(plugName.getText().toString(),
                        switchCode.toString(), houseCode.toString(), StateEnum.valueOf(stateSpinner.getSelectedItem().toString()));
                addPlugRequest = new AddPlugRequest(getActivity());
                addPlugRequest.setOnResponseListener(new OnResponseListener<Boolean>() {

                    @Override
                    public void onResponse(Boolean responseOK) {
                        if (responseOK) {
                            getActivity().finish();
                        } else {
                            onError(getResources().getString(R.string.failed_to_add_plug_message));
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
                addPlugRequest.send(plugTransferableData);

            }
        });
        return view;
    }
}
