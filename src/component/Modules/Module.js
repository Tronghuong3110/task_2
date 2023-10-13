import { useState, useEffect } from "react";
import 'bootstrap/dist/css/bootstrap.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import '../../sass/Module/Module.scss'
import { faCube, faMagnifyingGlass, faPlusCircle } from '@fortawesome/free-solid-svg-icons';
import { faPlusSquare } from '@fortawesome/free-regular-svg-icons';
import ModuleTable from './ModuleTable/ModuleTable';
import AddModule from './AddModule';
const Module = () =>{
    const [isOpen,openCloseWindow] = useState(false);

    // Hàm đóng mở cửa sổ thêm mới module hoặc edit module
    const handleOpenWindow = () =>{
        openCloseWindow(true)
    }
    const handleCloseWindow = () =>{
        openCloseWindow(false)
    }
    return (
        <div className="module">
            <div className='searchBar d-flex justify-content-between align-items-center'>
                <div className='searchName'>
                    <input type='text' id="module_name" placeholder='Search by name...'></input>
                    <FontAwesomeIcon icon={faMagnifyingGlass} flip="horizontal" ></FontAwesomeIcon>
                </div>
                <div className='addModule'>
                    <button className='addModuleButton d-flex' onClick={handleOpenWindow}>
                        <div className='addModuleButton-icon'>
                            <FontAwesomeIcon icon={faPlusSquare}></FontAwesomeIcon>
                        </div>
                        <div className='addModuleButton-text'>NEW MODULE</div>
                    </button>
                </div>
            </div>
            <ModuleTable></ModuleTable>
            {isOpen && <AddModule handleCloseWindow={handleCloseWindow}></AddModule>}
        </div>
    )
}
export default Module;