import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { TableRow, Table, TableHead, TableCell, TableBody } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons'
import {
} from '@fortawesome/free-solid-svg-icons'
import '../../sass/ModuleHistory/ModuleHistoryTable.scss'
import '../action/button/CheckBox'
import CheckBox from '../action/button/CheckBox';
const ModuleHistoryTable = () => {
    return (
        <div className='Module_History_Table'>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className="checkbox"></TableCell>
                        <TableCell className='time'>TIME</TableCell>
                        <TableCell className="probe_name">PROBE NAME</TableCell>
                        <TableCell className="module_name">MODULE</TableCell>
                        <TableCell className='title'>TITLE</TableCell>
                        <TableCell className='content'>CONTENT</TableCell>
                        <TableCell className='actions'></TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <TableRow>
                        <TableCell className="checkbox">
                            <CheckBox></CheckBox>
                        </TableCell>
                        <TableCell className='time'>01-10-2023 17:05:11</TableCell>
                        <TableCell className="probe_name">Module 01</TableCell>
                        <TableCell className="module_name">ping</TableCell>
                        <TableCell className='title'>-t 1.1.1.1</TableCell>
                        <TableCell className='content'>Danh cho quan ly</TableCell>
                        <TableCell className='actions'>
                            <div className='actions-container d-flex justify-content-around'>
                                <div className='action'>
                                    <button>
                                        <FontAwesomeIcon icon={faTrashCan} style={{ color: "white" }} />
                                    </button>
                                </div>
                            </div>
                        </TableCell>
                    </TableRow>
                </TableBody>
            </Table>
        </div>
    )
}
export default ModuleHistoryTable