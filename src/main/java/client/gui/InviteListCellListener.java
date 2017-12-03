package client.gui;


import common.UserID;

public interface InviteListCellListener {

    void acceptClicked(UserID user);

    void declineClicked(UserID user);

}
