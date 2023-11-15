import React from 'react';
import "../../sass/Sidebar.scss";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import "@fontsource/montserrat";
import {
    faChartLine,
    faClockRotateLeft,
    faCube,
    faDisplay,
    faHouse,faBars
} from '@fortawesome/free-solid-svg-icons'
import { Link } from 'react-router-dom';
const Sidebar = (props) => {
    const {isHide,setHideSideBar} = props
    // const [isSetScreen, setScreen] = useState("")
    const hideOpenSideBar = ()=>{
        let elements = document.querySelectorAll(".sidebar-text")
        if(isHide){
            elements.forEach(element => {
                element.style.display = "block"
            });
            setHideSideBar(false)
        }
        else{
            elements.forEach(element => {
                element.style.display = "none"
            });
            setHideSideBar(true)
        }
    }
    return (
        <div className="sidebar">
            <div className="sidebar-title d-flex justify-content-center">
                <div className='sidebar-title-symbol'>
                    <button className='hideSideBar'
                    onClick={hideOpenSideBar}
                    >
                        <FontAwesomeIcon icon={faBars}></FontAwesomeIcon>
                    </button>
                </div>
                <div className='sidebar-title-text sidebar-text'>
                    MAIN MENU
                </div>
            </div>
            <hr></hr>
            <div className="sidebar-selections">
                <div className="selection d-flex" id="dashboard">
                    <Link to={"/"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faHouse} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text sidebar-text">Dashbroad</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="modules">
                    <Link to={"/modules"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faCube} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text sidebar-text">Modules</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="probes">
                    <Link to={"/probe"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faDisplay} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text sidebar-text">Probes</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="history">
                    <Link to={"/module_history/0"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faClockRotateLeft} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text sidebar-text">History</div>
                    </Link>
                </div>
                <div className="selection d-flex" id="statistic">
                    <Link to={"/statistic"}>
                        <div className="selection-symbol d-flex align-items-center">
                            <FontAwesomeIcon icon={faChartLine} style={{ color: "#ffffff", fontSize: "1em" }} />
                        </div>
                        <div className="selection-text sidebar-text">Statistic</div>
                    </Link>
                </div>
            </div>
        </div>
    )
}
export default Sidebar;