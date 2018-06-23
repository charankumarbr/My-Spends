package in.phoenix.myspends.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.phoenix.myspends.R;
import in.phoenix.myspends.customview.CustomTextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ImpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImpFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;


    public ImpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ImpFragment.
     */
    public static ImpFragment newInstance(String param1) {
        ImpFragment fragment = new ImpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_imp, container, false);
        CustomTextView tvImp = view.findViewById(R.id.fi_tv_imp);
        tvImp.setText(mParam1);
        return view;
    }

}
