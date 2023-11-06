import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { TableRow, Table, TableHead, TableCell, TableBody, Checkbox } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons'
import { faCircleCheck } from '@fortawesome/free-solid-svg-icons'
import '../../../sass/ModuleHistory/ModuleHistoryTable.scss'
import {IP} from '../../Layout/constaints'
const ModuleHistoryTable = (props) => {
    const {condition} = props
    const [moduleHistories, setModuleHistories] = useState([])
    useEffect(() => {
        fetch("http://" + IP + ":8081/api/v1/moduleHistories")
            .then(response => response.json())
            .then(data => {
                setModuleHistories(data)
            })
            .catch(err => console.log(err))
    },[condition])
    return (
        <div className='Module_History_Table'>
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
                        moduleHistories.map(item => {
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
                                        ></Checkbox>
                                    </TableCell>
                                    <TableCell className='time'>{item.atTime}</TableCell>
                                    <TableCell className="probe_name">{item.probeName}</TableCell>
                                    <TableCell className="module_name">{item.moduleName}</TableCell>
                                    <TableCell className='content'>{item.content}</TableCell>
                                    <TableCell className='actions'>
                                        <div className='actions-container d-flex justify-content-around'>
                                            <div className='action'>
                                                <button>
                                                    <FontAwesomeIcon icon={faCircleCheck} style={{ color: "#0FA958" }} />
                                                    {/* <FontAwesomeIcon icon="fa-regular fa-circle-check" style={{color: "#c1c3cf",}} /> */}
                                                </button>
                                            </div>
                                            <div className='action'>
                                                <button>
                                                    <FontAwesomeIcon icon={faTrashCan} style={{ color: "white" }} />
                                                </button>
                                            </div>
                                        </div>
                                    </TableCell>
                                </TableRow>

                            )
                        })
                    }
                </TableBody>
            </Table>
        </div>
    )
}
export default ModuleHistoryTable