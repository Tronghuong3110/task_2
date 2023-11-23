import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { TableRow, Table, TableHead, TableCell, TableBody, Checkbox } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faTrashCan } from '@fortawesome/free-regular-svg-icons'
import { faArrowRotateLeft, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons'
import '../../../sass/RecycleBin/RecycleBinTable.scss'
import { IP } from '../../Layout/constaints'
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotifyContainer from './NotifyContainer';
import SmallConfirm from '../../action/SmallConfirm';
const RecycleBinTable = (props) => {
    const { probes, getProbesInBin, nameCondition } = props
    const [selectedProbes, setSelectedProbes] = useState([])
    const [notifyList, setNotifyList] = useState([])
    const [notifyScreen, setOpenNotifyScreen] = useState(false)
    const [confirmScreen, setOpenConfirmScreen] = useState(false)
    const [action, setAction] = useState({
        "module": null,
        "action": "",
        "note": ""
    })
    const tickAllDisplay = (event, checked) => {
        let nodes = document.querySelectorAll("table tbody tr td input[type ='checkbox']")
        let arr = []
        if (checked === true) {
            nodes.forEach(ele => {
                arr.push(ele.id)
            })
            setSelectedProbes(arr)
        }
        else setSelectedProbes([])
    }
    const isSelected = (id) => {
        if (selectedProbes.find(num => num == id) == undefined) return false;
        else return true;
    }
    const addOrRemoveToSelectedList = (event, checked, id) => {
        if (checked === true) {
            setSelectedProbes([...selectedProbes, id])
            console.log([...selectedProbes, id])
        }
        else {
            setSelectedProbes(selectedProbes.filter(item => item != id))
            console.log(selectedProbes.filter(item => item != id))
        }
    }
    const notify = (message, status) => {
        if (status == 1) {
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
        else if (status == 0) {
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
    const openConfirmWindow = () => {
        setOpenConfirmScreen(true)
    }
    const closeConfirmWindow = () => {
        setOpenConfirmScreen(false)
    }
    const actionProbe = (id, action) => {
        let param = []
        if (!Array.isArray(id)) {
            param = [id]
        }
        else param = id
        console.log(param)
        let options = {
            method: action == "remove" ? "DELETE" : "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                ids: param
            })
        }
        let api = "http://" + IP + ":8081/api/v1/probe"
        if (action === "remove") api += "/remove"
        fetch(api, options)
            .then(response => response.json())
            .then(data => {
                if (data.length == 1) {
                    notify(data[0].message, data[0].code)
                    setSelectedProbes([])
                    getProbesInBin(nameCondition)
                }
                else {
                    setOpenNotifyScreen(true)
                    setSelectedProbes([])
                    setNotifyList(data)
                    getProbesInBin(nameCondition)
                }
            })
            .catch(err => console.log(err))
    }
    // const removeProbe = (id) => {
    //     let param = []
    //     if (!Array.isArray(id)) {
    //         param = [id]
    //     }
    //     else param = id
    //     console.log(param)
    //     let options = {
    //         method: "DELETE",
    //         headers: {
    //             "Content-Type": "application/json"
    //         },
    //         body: JSON.stringify({
    //             ids: param
    //         })
    //     }
    //     fetch("http://" + IP + ":8081/api/v1/probe/remove", options)
    //         .then(response => response.json())
    //         .then(data => {
    //             if (data.length == 1) {
    //                 notify(data[0].message, data[0].code)
    //             }
    //             else {
    //                 setOpenNotifyScreen(true)
    //                 setNotifyList(data)
    //                 setSelectedProbes([])
    //                 getProbesInBin(nameCondition)
    //             }
    //         })
    //         .catch(err => console.log(err))
    // }
    const actionWithProbe = (id, action) => {
        let arr;
        if (Array.isArray(id)) arr = id;
        else arr = [id]
        setAction({
            "module": arr,
            "action": action,
            "note": action=="remove"?"The devices will be permanently deleted from the system":""
        })
        openConfirmWindow()
    }
    return (
        <div className='RecycleBinTable'>
            <div className="actionBar d-flex align-items-center">
                <div className="checkAll">
                    <Checkbox
                        sx={{
                            color: 'white',
                            '&.Mui-checked': {
                                color: 'white',
                            },
                        }}
                        onChange={tickAllDisplay}
                    >
                    </Checkbox>
                </div>
                <div className="refreshButton">
                    <button
                        onClick={() => {
                            actionWithProbe(selectedProbes, "recover")
                        }}
                    >
                        <FontAwesomeIcon icon={faArrowRotateBack}></FontAwesomeIcon>
                    </button>
                </div>
                <div className="deleteButton">
                    <button onClick={() => {
                        actionWithProbe(selectedProbes, "remove")
                    }}>
                        <FontAwesomeIcon icon={faTrashCan}></FontAwesomeIcon>
                    </button>
                </div>
            </div>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className="checkbox"></TableCell>
                        <TableCell className='id'>ID</TableCell>
                        <TableCell className="name">PROBE NAME</TableCell>
                        <TableCell className="ip_address">IP ADDRESS</TableCell>
                        <TableCell className='location'>LOCATION</TableCell>
                        <TableCell className='area'>AREA</TableCell>
                        <TableCell className='description'>DESCRIPTION</TableCell>
                        <TableCell className='action'>ACTION</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        probes.length !== 0 ? (probes.map(item => {
                            return (
                                <TableRow key={item.id}>
                                    <TableCell className="checkbox">
                                        <Checkbox
                                            sx={{
                                                color: 'white',
                                                '&.Mui-checked': {
                                                    color: 'white',
                                                },
                                            }}
                                            id={item.id}
                                            checked={isSelected(item.id)}
                                            onChange={(event, checked) => addOrRemoveToSelectedList(event, checked, item.id)}
                                        ></Checkbox>
                                    </TableCell>
                                    <TableCell className='id'>{item.id}</TableCell>
                                    <TableCell className="name">{item.name}</TableCell>
                                    <TableCell className="ip_address">{item.ipAddress}</TableCell>
                                    <TableCell className='location'>{item.location}</TableCell>
                                    <TableCell className='area'>{item.area}</TableCell>
                                    <TableCell className='description'>{item.description}</TableCell>
                                    <TableCell className='actions'>
                                        <div className='actions-container d-flex justify-content-around'>
                                            <div className='action'>
                                                <button onClick={() => {
                                                    actionWithProbe(item.id, "recover")
                                                }}>
                                                    <FontAwesomeIcon icon={faArrowRotateLeft} style={{ color: "#1ae6ea", }} />
                                                </button>
                                            </div>
                                            <div className='action'>
                                                <button onClick={() => {
                                                    actionWithProbe(item.id, "remove")
                                                }}>
                                                    <FontAwesomeIcon icon={faTrashCan} style={{ color: "white" }} />
                                                </button>
                                            </div>
                                        </div>
                                    </TableCell>
                                </TableRow>

                            )
                        })) : (
                            <TableRow style={{ border: "none" }}>
                                <TableCell colSpan={15} style={{ padding: "20px", fontWeight: "500", fontSize: "1.2em" }} >There is no history in the list pass the conditions</TableCell>
                            </TableRow>
                        )
                    }
                </TableBody>
            </Table>
            {notifyScreen && <NotifyContainer notifyList={notifyList} setOpenNotifyScreen={setOpenNotifyScreen} handleCloseWindow={closeConfirmWindow} ></NotifyContainer>}
            {confirmScreen && <SmallConfirm setOpenConfirmScreen={setOpenConfirmScreen} action={action} handleFunction={actionProbe} object="probe" ></SmallConfirm>}
        </div>
    )
}
export default RecycleBinTable