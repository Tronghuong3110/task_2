import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCube, faMagnifyingGlass, faClockRotateLeft, faArrowRotateBack } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare, faTrashCan } from '@fortawesome/free-regular-svg-icons';
import '../../sass/ModuleHistory/ModuleHistory.scss'
import ModuleHistoryTable from "./ModuleHistoryTable/ModuleHistoryTable";
import DropdownWithInput from '../action/DropdownWithInput';
import { Checkbox } from "@mui/material";



const ModuleHistory = () => {
    return (
        <div className="modulesHistory">
            <div className='searchBar d-flex justify-content-between align-items-center'>
                <div className="searchDate">
                    <input type="date" placeholder="Choose date"></input>
                </div>
                <div className="searchProbe">
                    {/* <DropdownWithInput></DropdownWithInput> */}
                </div>
                <div className="searchModule">
                    <select>
                        <option>Choose module</option>
                        <option>1</option>
                        <option>2</option>
                        <option>3</option>
                    </select>
                </div>
                <div className='searchTitle'>
                    <input type='text' id="module_name" placeholder='Search by name...'></input>
                </div>
                <button className='searchButton d-flex' >
                    <div className='searchButton-icon'>
                        <FontAwesomeIcon icon={faMagnifyingGlass} rotation={90}></FontAwesomeIcon>
                    </div>
                    <div className='searchButton-text'>Search</div>
                </button>
            </div>
            <div className="actionBar d-flex align-items-center">
                <div className="checkAll">
                    <Checkbox
                        sx={{
                            color: 'white',
                            '&.Mui-checked': {
                                color: 'white',
                            },
                        }}
                    >

                    </Checkbox>
                </div>
                <div className="refreshButton">
                    <button>
                        <FontAwesomeIcon icon={faArrowRotateBack}></FontAwesomeIcon>
                    </button>
                </div>
                <div className="deleteButton">
                    <button>
                        <FontAwesomeIcon icon={faTrashCan}></FontAwesomeIcon>
                    </button>
                </div>
            </div>
            <ModuleHistoryTable></ModuleHistoryTable>
        </div>
    )
}
export default ModuleHistory;