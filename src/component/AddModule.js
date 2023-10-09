import { useState, useEffect } from 'react';
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCircleXmark, faFolderOpen, faNoteSticky } from '@fortawesome/free-regular-svg-icons'
import {
    faFloppyDisk, faCube, faTerminal
} from '@fortawesome/free-solid-svg-icons'
import '../sass/Module/AddModule.scss';
const AddModule = ({handleCloseWindow}) => {

    // Thêm mới một module mẫu
    const addNewModule =()=>{
        handleCloseWindow();
    }
    return (
        <div className="addModuleScreen">
            <div className="addModule">
                <div className='btn-container d-flex justify-content-end'>
                    <button className='close-btn d-flex align-items-center' onClick={handleCloseWindow}>
                        <FontAwesomeIcon icon={faCircleXmark} />
                    </button>
                </div>
                {/* Module_Name */}
                <div className="field ">
                    <div className='input_container'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faCube} />
                            <div className='input_container-icon-text'>MODULE</div>
                        </div>
                        <div className='input_container-input'>
                            <input type='text' id="module_name" placeholder='Type module name...'></input>
                        </div>
                    </div>
                    <div className="field ">
                        <div className='input_container exception'>
                            <div className='input_container-icon d-flex align-items-center'>
                                <FontAwesomeIcon icon={faTerminal} />
                                <div className='input_container-icon-text'>COMMAND</div>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput' type='text' placeholder='Path...' id='path'></input>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput' type='text' placeholder='Caption...' id='caption'></input>
                            </div>
                            <div className='input_container-input'>
                                <input className='commandInput exception' type='text' placeholder='Default argument' id='argument'></input>
                            </div>
                        </div>
                    </div>
                </div>
                {/*Note*/}
                <div className="field d-flex justify-content-between">
                    <div className='input_container exception'>
                        <div className='input_container-icon d-flex align-items-center'>
                            <FontAwesomeIcon icon={faNoteSticky} />
                            <div className='input_container-icon-text'>NOTE</div>
                        </div>
                        <div className='input_container-input'>
                            <textarea placeholder='Take note here...' id='note'></textarea>
                        </div>
                    </div>
                </div>
                {/* Button */}
                <div className='btn-container d-flex justify-content-end'>
                    <button className='btn btn-success d-flex align-items-center' onClick={addNewModule} >
                        <div className='btn-icon d-flex align-items-center' >
                            <FontAwesomeIcon icon={faFloppyDisk} />
                        </div>
                        <div className='btn-text' >Save</div>
                    </button>
                </div>
            </div>
        </div>
    )
}
export default AddModule; 