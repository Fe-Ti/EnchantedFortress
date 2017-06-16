package hr.kravarscan.enchantedfortress;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hr.kravarscan.enchantedfortress.storage.SaveLoad;

public class MainMenuFragment extends Fragment {

    private OnFragmentInteractionListener listener;

    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_main_menu, container, false);

        View continueButton = layout.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onContinue();
            }
        });
        if (!SaveLoad.get().hasAutosave(this.getContext()))
            continueButton.setVisibility(View.GONE);

        layout.findViewById(R.id.newGameButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onNewGame();
                    }
                }
        );

        layout.findViewById(R.id.helpButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onHelp();
                    }
                }
        );

        layout.findViewById(R.id.aboutButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onAbout();
                    }
                }
        );

        return layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
        void onContinue();
        void onNewGame();
        void onHelp();
        void onAbout();
    }
}
