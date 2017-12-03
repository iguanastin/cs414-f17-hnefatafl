package edu.colostate.cs.cs414.hnefatafl.client.gui;


import edu.colostate.cs.cs414.hnefatafl.common.UserID;

public interface InviteListCellListener {

    void acceptClicked(UserID user);

    void declineClicked(UserID user);

}
