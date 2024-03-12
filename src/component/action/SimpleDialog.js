import * as React from 'react';
import PropTypes from 'prop-types';
import Button from '@mui/material/Button';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import DialogTitle from '@mui/material/DialogTitle';
import Dialog from '@mui/material/Dialog';
import {  faTrashCan } from '@fortawesome/free-regular-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Checkbox } from '@mui/material';
import { IP } from '../Layout/constaints';
import { toast } from 'react-toastify';


function SimpleDialog(props) {
    const { onClose, open } = props;
    const [choosenIp, setChoosenIP] = React.useState({
        "ipDbLevel1":null,
        "ipDbLevel2":null
    })
    const handleClose = () => {
        onClose(choosenIp);
    };
    const handleCloseDoNothing= () =>{
        onClose(1)
    }



    return (
        <Dialog onClose={handleCloseDoNothing} open={open}>
            <DialogTitle>Choose IP database to delete</DialogTitle>
            <List sx={{ pt: 0 }}>
                <ListItem disableGutters >
                    <Checkbox disabled={props.ipDbLevel1 === "" ? true : false} value={props.ipDbLevel1}
                        onChange={(event) => {
                            setChoosenIP({
                                ...choosenIp,
                                "ipDbLevel1": event.target.checked === true ? props.ipDbLevel1 : null
                            })
                        }}

                    />
                    <ListItemText primary={props.ipDbLevel1 === "" ? "IP DB Level 1 : Deleted" : `IP DB Level 1 :  ${props.ipDbLevel1} `} />
                </ListItem>
                <ListItem disableGutters >
                    <Checkbox disabled={props.ipDbLevel2 === "" ? true : false} value={props.ipDbLevel2}
                        onChange={(event) => {
                            setChoosenIP({
                                ...choosenIp,
                                "ipDbLevel2": event.target.checked === true ? props.ipDbLevel2 : null
                            })
                        }}

                    />
                    <ListItemText primary={props.ipDbLevel2 === "" ? "IP DB Level 2 : Deleted" : `IP DB Level 2 : ${props.ipDbLevel2}`} />
                </ListItem>

            </List>
            <Button sx={{
                background: "rgb(214 19 62 / 81%)", color: "white",
                ":hover": {
                    backgroundColor: "#ff0000",
                    textDecoration: "none"
                }

            }} onClick={handleClose} >Delete</Button>
        </Dialog>
    );
}

SimpleDialog.propTypes = {
    onClose: PropTypes.func.isRequired,
    open: PropTypes.bool.isRequired,
    selectedValue: PropTypes.string,
};

export default function SimpleDialogDemo({ ipDbLevel1, ipDbLevel2, databaseName, idInfo }) {
    const [open, setOpen] = React.useState(false);
    const [selectedValue, setSelectedValue] = React.useState();
    const notify = (message, status) => {
        if (status === 1) {
            toast.success(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else if (status === 0) {
            toast.error(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }
        else {
            toast.warn(message, {
                position: "top-center",
                autoClose: 4000,
                hideProgressBar: false,
                closeOnClick: true,
                draggable: true,
                progress: undefined,
                theme: "colored",
            })
        }

    }
    const handleClickOpen = () => {
        setOpen(true);
    };

    const handleClose = (value) => {
        if(value!==1){
            if(value.ipDbLevel1!=null){
                doDelete(value.ipDbLevel1)
            }
            if(value.ipDbLevel2!=null){
                doDelete(value.ipDbLevel2)
            }
        }
        setOpen(false);
        
    };
    const doDelete = (ipServer) => {
        let options = {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            }
        }
        fetch(IP + "/api/v1/delete/database?ipServer=" + ipServer + "&databaseName=" + databaseName + "&idInfo=" + idInfo, options)
            .then(response => response.json())
            .then(data => {
                notify(data.message, data.code)
            })
            .catch(err => console.log(err))
    }

    return (
        <div>
            <button onClick={handleClickOpen}>
                <FontAwesomeIcon icon={faTrashCan} style={{ color: "red" }} />
            </button>
            <SimpleDialog
                selectedValue={selectedValue}
                setSelectedValue={setSelectedValue}
                open={open}
                onClose={handleClose}
                ipDbLevel1={ipDbLevel1}
                ipDbLevel2={ipDbLevel2}
            />
        </div>
        
    );
}
