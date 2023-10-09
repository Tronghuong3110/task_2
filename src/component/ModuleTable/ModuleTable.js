import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import '../../sass/Module/ModuleTable/ModuleTable.scss'
import { TableRow, Table, TableHead, TableCell, TableBody } from "@mui/material";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPenToSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons'
import {
} from '@fortawesome/free-solid-svg-icons'

const ModuleTable = () => {
    return (
        <div className='Module_Table'>
            <Table>
                <TableHead>
                    <TableRow>
                        <TableCell className="id">Id</TableCell>
                        <TableCell className="module_name">Module name</TableCell>
                        <TableCell className="caption">Caption</TableCell>
                        <TableCell className='arg'>Default argument</TableCell>
                        <TableCell className='note'>Note</TableCell>
                        <TableCell className='actions'>Action</TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    <TableRow>
                        <TableCell className="id">1</TableCell>
                        <TableCell className="module_name">Module 01</TableCell>
                        <TableCell className="caption">ping</TableCell>
                        <TableCell className='arg'>-t 1.1.1.1</TableCell>
                        <TableCell className='note'>Danh cho quan ly</TableCell>
                        <TableCell className='actions'>
                            <div className='actions-container d-flex justify-content-around'>
                                <div className='action'>
                                    <button>
                                        <FontAwesomeIcon icon={faPenToSquare} style={{ color: "#699BF7" }} />
                                    </button>
                                </div>
                                <div className='action'>
                                    <button>
                                        <FontAwesomeIcon icon={faTrashCan} style={{ color: "#FFD233" }} />
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
export default ModuleTable;