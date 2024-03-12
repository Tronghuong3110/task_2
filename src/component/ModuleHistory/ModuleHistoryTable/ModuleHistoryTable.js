import { useState } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { TableRow, Table, TableHead, TableCell, TableBody, Checkbox } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleCheck as regularFaCircleCheck, faTrashCan } from '@fortawesome/free-regular-svg-icons'
import { faCircleCheck as solidFaCircleCheck, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons'
import '../../../sass/ModuleHistory/ModuleHistoryTable.scss'
import { IP } from '../../Layout/constaints'
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
const ModuleHistoryTable = (props) => {
    const { moduleHistories, getModuleHistory,conditions } = props
    const [selectedModuleHistory, setSelectedModuleHistory] = useState([])
    const updateACK = (id) => {
        let options = {
            method: "PUT"
        }
        fetch( IP + "/api/v1//moduleHistory?idModuleHistory=" + id, options)
            .then(respose => respose.text())
            .then(data => {
                getModuleHistory(conditions)
            })
    }
    const tickAllDisplay = (event, checked) => {
        let nodes = document.querySelectorAll("table tbody tr td input[type ='checkbox']")
        let arr = []
        if (checked === true) {
            nodes.forEach(ele => {
                arr.push(ele.id)
            })
            setSelectedModuleHistory(arr)
        }
        else setSelectedModuleHistory([])
    }
    const isSelected = (id) => {
        if (selectedModuleHistory.find(num => num === id) === undefined) return false;
        else return true;
    }
    const addOrRemoveToSelectedList = (event, checked, id) => {
        if (checked === true) {
            setSelectedModuleHistory([...selectedModuleHistory, id])
            console.log([...selectedModuleHistory, id])
        }
        else {
            setSelectedModuleHistory(selectedModuleHistory.filter(item => item !== id))
            console.log(selectedModuleHistory.filter(item => item !== id))
        }
    }
    const removeModuleHistory = (id) => {
        let param = []
        if (!Array.isArray(id)) {
            param = [id]
        }
        else param = id
        console.log(param)
        let options = {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                idsStr: param
            })
        }
        fetch( IP + "/api/v1/module/history", options)
            .then(response => response.json())
            .then(data => {
                console.log(data)
                notify(data.message, data.code)
                getModuleHistory()
            })
            .catch(err => console.log(err))
    }
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
    return (
        <div className='Module_History_Table'>
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
                    <button>
                        <FontAwesomeIcon icon={faArrowRotateBack}></FontAwesomeIcon>
                    </button>
                </div>
                <div className="deleteButton">
                    <button onClick={() => {
                        removeModuleHistory(selectedModuleHistory)
                    }}>
                        <FontAwesomeIcon icon={faTrashCan}></FontAwesomeIcon>
                    </button>
                </div>
            </div>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className="checkbox"></TableCell>
                        <TableCell className='time'>TIME</TableCell>
                        <TableCell className="probe_name">PROBE</TableCell>
                        <TableCell className="module_name">MODULE</TableCell>
                        <TableCell className='content'>CONTENT</TableCell>
                        <TableCell className='actions'></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {
                        moduleHistories.length !== 0 ? (moduleHistories.map(item => {
                            return (
                                <TableRow key={item.idModuleHistory}>
                                    <TableCell className="checkbox">
                                        <Checkbox
                                            sx={{
                                                color: 'white',
                                                '&.Mui-checked': {
                                                    color: 'white',
                                                },
                                            }}
                                            id={item.idModuleHistory}
                                            checked={isSelected(item.idModuleHistory)}
                                            onChange={(event, checked) => addOrRemoveToSelectedList(event, checked, item.idModuleHistory)}
                                        ></Checkbox>
                                    </TableCell>
                                    <TableCell className='time'>{item.atTime}</TableCell>
                                    <TableCell className="probe_name">{item.probeName}</TableCell>
                                    <TableCell className="module_name">{item.moduleName}</TableCell>
                                    <TableCell className='content'>{item.content}</TableCell>
                                    <TableCell className='actions'>
                                        <div className='actions-container d-flex justify-content-around'>
                                            <div className='action'>
                                                <button disabled={item.ack} onClick={() => {
                                                    updateACK(item.idModuleHistory)
                                                }}>
                                                    {item.ack === 0 ? (<FontAwesomeIcon icon={regularFaCircleCheck} style={{ color: "#c1c3cf", }} />) : (<FontAwesomeIcon icon={solidFaCircleCheck} style={{ color: "#0FA958" }} />)}
                                                </button>
                                            </div>
                                            <div className='action'>
                                                <button onClick={() => {
                                                    removeModuleHistory(item.idModuleHistory)
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
                                <TableCell colSpan={15} style={{padding:"20px", fontWeight:"500", fontSize:"1.2em" }} >There is no history in the list pass the conditions</TableCell>
                            </TableRow>
                        )
                    }
                </TableBody>
            </Table>
        </div>
    )
}
export default ModuleHistoryTable