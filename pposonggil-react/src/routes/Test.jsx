import React from "react";
import styled from "styled-components";
import { useNavigate } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faListUl } from "@fortawesome/free-solid-svg-icons";


const Button = styled.button`
padding: 10px 20px;
background-color: tomato;
color: white;
border: none;
border-radius: 5px;
cursor: pointer;
display: flex;
align-items: center;
font-size: 18px;
margin-top: 20px;

&:hover {
  background-color: darkred;
}

svg {
  margin-left: 10px;
}
`;

function Test() {
    const handleCreateMember = async () => {
        const memberData = {
            name: "name1",
            nickName: "nickName1",
            phone: "01011111111"
        };

        try {
            const response = await fetch('http://localhost:8080/api/member', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(memberData)
            });

            if (response.ok) {
                const data = await response.json();
                console.log('Member created successfully:', data);
                alert('Member created successfully');
            } else {
                console.error('Failed to create member');
                alert('Failed to create member');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('Error occurred while creating member');
        }
    };

    return (
        <React.Fragment>

            <Button onClick={handleCreateMember}>
                회원 생성
            </Button>

        </React.Fragment>
    );
}

export default Test;